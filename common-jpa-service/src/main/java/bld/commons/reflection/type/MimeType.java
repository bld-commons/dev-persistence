package bld.commons.reflection.type;

public enum MimeType {
	
	none("",""),
	aac("aac","audio/aac"),
	abw("abw","application/x-abiword"),
	arc("arc","application/x-freearc"),
	avi("avi","video/x-msvideo"),
	azw("azw","application/vnd.amazon.ebook"),
	bin("bin","application/octet-stream"),
	bmp("bmp","image/bmp"),
	bz("bz","application/x-bzip"),
	bz2("bz2","application/x-bzip2"),
	csh("csh","application/x-csh"),
	css("css","text/css"),
	csv("csv","text/csv"),
	doc("doc","application/msword"),
	docx("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	eot("eot","application/vnd.ms-fontobject"),
	epub("epub","application/epub+zip"),
	gz("gz","application/gzip"),
	gif("gif","image/gif"),
	htm("htm","text/html"),
	ico("ico","image/vnd.microsoft.icon"),
	ics("ics","text/calendar"),
	jar("jar","application/java-archive"),
	jpg ("jpg ","image/jpeg"),
	js("js","text/javascript"),
	json("json","application/json"),
	jsonld("jsonld","application/ld+json"),
	mid("mid","audio/midi audio/x-midi "),
	mp3("mp3","audio/mpeg"),
	mpeg("mpeg","video/mpeg"),
	mpkg("mpkg","application/vnd.apple.installer+xml"),
	odp("odp","application/vnd.oasis.opendocument.presentation"),
	ods("ods","application/vnd.oasis.opendocument.spreadsheet"),
	odt("odt","application/vnd.oasis.opendocument.text"),
	oga("oga","audio/ogg"),
	ogv("ogv","video/ogg"),
	ogx("ogx","application/ogg"),
	opus("opus","audio/opus"),
	otf("otf","font/otf"),
	png("png","image/png"),
	pdf("pdf","application/pdf"),
	php("php","application/x-httpd-php"),
	ppt("ppt","application/vnd.ms-powerpoint"),
	pptx("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	rar("rar","application/vnd.rar"),
	rtf("rtf","application/rtf"),
	sh("sh","application/x-sh"),
	svg("svg","image/svg+xml"),
	swf("swf","application/x-shockwave-flash"),
	tar("tar","application/x-tar"),
	tif("tif","image/tiff"),
	ts("ts","video/mp2t"),
	ttf("ttf","font/ttf"),
	txt("txt","text/plain"),
	vsd("vsd","application/vnd.visio"),
	wav("wav","audio/wav"),
	weba("weba","audio/webm"),
	webm("webm","video/webm"),
	webp("webp","image/webp"),
	woff("woff","font/woff"),
	woff2("woff2","font/woff2"),
	xhtml("xhtml","application/xhtml+xml"),
	xls("xls","application/vnd.ms-excel"),
	xlsx("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	xml("xml","application/xml"),
	xul("xul","application/vnd.mozilla.xul+xml"),
	zip("zip","application/zip"),
	three_gp("3gp","video/3gpp audio/3gpp"),
	three_g2("3g2","video/3gpp2 audio/3gpp2"),
	seven_z("7z","application/x-7z-compressed"),
	html("html","text/html"),
	jpeg("jpeg","image/jpeg"),
	midi ("midi ","audio/midi audio/x-midi "),
	mjs("mjs","text/javascript"),
	tiff("tiff","image/tiff"),
	log("log","text/plain");

	


	private String extension;
	
	private String mimeType;

	private MimeType(String extension, String mimeType) {
		this.extension = extension;
		this.mimeType = mimeType;
	}

	public String getExtension() {
		return extension;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	
	
	
	

}
