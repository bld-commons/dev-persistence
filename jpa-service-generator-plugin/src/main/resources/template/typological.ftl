package ${modelClass.packageClass};

<#list modelClass.imports as importClass>
<#if importClass??>
import ${importClass};
</#if>
</#list>

<#list modelClass.annotations as annotation>
${annotation.toString()}
</#list>
${modelClass.toString()}{



    public ${modelClass.name}(){
    }

	

	<#list modelClass.fields as field>
	<#list field.annotations as annotationField>
	<#if annotationField??>
	${annotationField.toString()}
	</#if>	
	</#list>
    ${field.toString()}
    
	</#list>
	
	<#list modelClass.fields as field>
	<#if field.getterSetter == true >
    public ${field.type} get${field.name?cap_first}(){
        return ${field.name};
    }
    
    public void set${field.name?cap_first}(${field.type} ${field.name}){
        this.${field.name} = ${field.name};
    }
    </#if>
    </#list>

}