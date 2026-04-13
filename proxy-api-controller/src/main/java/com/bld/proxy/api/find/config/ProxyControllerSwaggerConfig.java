package com.bld.proxy.api.find.config;

import com.bld.proxy.api.find.annotations.ApiFindController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Auto-configuration that fixes the Swagger/SpringDoc tag names for
 * {@link ApiFindController} proxy beans.
 *
 * <p>JDK dynamic proxies have auto-generated class names (e.g. {@code $Proxy265}).
 * SpringDoc converts them to kebab-case tags like {@code $-proxy-265}, which is
 * not useful. This customizer replaces those tags with the kebab-case name of the
 * original interface (e.g. {@code service-type-find-controller}).</p>
 *
 * <p>This configuration is activated only when SpringDoc is on the classpath.
 * It is picked up automatically via the {@code @ComponentScan} in
 * {@link ProxyApiFindConfig}.</p>
 *
 * @author Francesco Baldi
 */
@Configuration
@ConditionalOnClass(name = "org.springdoc.core.customizers.OpenApiCustomizer")
public class ProxyControllerSwaggerConfig {

    @Bean
    OpenApiCustomizer proxyControllerTagCustomizer(ApplicationContext ctx) {
        return openApi -> {
            Map<String, String> tagRenameMap = buildTagRenameMap(ctx, openApi);
            if (tagRenameMap.isEmpty()) {
                return;
            }
            renameOperationTags(openApi, tagRenameMap);
            renameTopLevelTags(openApi, tagRenameMap);
        };
    }

    /**
     * Builds a map from the wrong proxy tag names to the correct interface tag names.
     * <p>
     * Strategy: for each proxy bean, find the operations whose path starts with the
     * interface's {@code @RequestMapping} prefix, read their current (wrong) tag, and
     * map it to the kebab-case interface simple name.
     */
    private Map<String, String> buildTagRenameMap(ApplicationContext ctx, OpenAPI openApi) {
        Map<String, String> tagRenameMap = new HashMap<>();

        String[] beanNames = ctx.getBeanNamesForAnnotation(ApiFindController.class);
        for (String beanName : beanNames) {
            Object bean = ctx.getBean(beanName);
            if (!Proxy.isProxyClass(bean.getClass())) {
                continue;
            }
            for (Class<?> iface : bean.getClass().getInterfaces()) {
                if (!iface.isAnnotationPresent(ApiFindController.class)) {
                    continue;
                }
                RequestMapping rm = iface.getAnnotation(RequestMapping.class);
                if (rm == null || rm.value().length == 0) {
                    break;
                }
                String pathPrefix = rm.value()[0];
                String correctTag = toKebabCase(iface.getSimpleName());

                // Find the current (wrong) tag by matching operation paths
                if (openApi.getPaths() != null) {
                    for (Map.Entry<String, PathItem> entry : openApi.getPaths().entrySet()) {
                        if (!entry.getKey().startsWith(pathPrefix)) {
                            continue;
                        }
                        for (Operation op : entry.getValue().readOperations()) {
                            if (op.getTags() != null) {
                                op.getTags().forEach(t -> tagRenameMap.put(t, correctTag));
                            }
                        }
                    }
                }
                break;
            }
        }
        return tagRenameMap;
    }

    private void renameOperationTags(OpenAPI openApi, Map<String, String> tagRenameMap) {
        if (openApi.getPaths() == null) {
            return;
        }
        openApi.getPaths().values().forEach(pathItem ->
            pathItem.readOperations().forEach(op -> {
                if (op.getTags() != null) {
                    List<String> renamed = op.getTags().stream()
                            .map(t -> tagRenameMap.getOrDefault(t, t))
                            .collect(Collectors.toList());
                    op.setTags(renamed);
                }
            })
        );
    }

    private void renameTopLevelTags(OpenAPI openApi, Map<String, String> tagRenameMap) {
        if (openApi.getTags() == null) {
            return;
        }
        openApi.getTags().forEach(tag -> {
            String newName = tagRenameMap.get(tag.getName());
            if (newName != null) {
                tag.setName(newName);
            }
        });
    }

    /**
     * Converts a PascalCase or camelCase class simple name to kebab-case,
     * replicating what SpringDoc would produce for a normal controller.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code ServiceTypeFindController} → {@code service-type-find-controller}</li>
     *   <li>{@code OrderController} → {@code order-controller}</li>
     * </ul>
     */
    private static String toKebabCase(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('-');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
