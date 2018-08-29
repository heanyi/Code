package testHttpClient;

import java.io.StringWriter;
import java.io.Writer;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerUtils{
	
	private static Configuration config = new Configuration(Configuration.VERSION_2_3_27);
	
	/**
	 * 模版处理
	 * @param name
	 * @param templateSource
	 * @param dataModel
	 * @return
	 * @throws Exception
	 */
	public static String process(String name, String templateSource, Object dataModel) throws Exception{
		Template template = config.getTemplate(name, null, "UTF-8", true, true);
		if(template == null){
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate(name, templateSource);
			config.setTemplateLoader(stringLoader);
			config.setNumberFormat("#");
			
			template = config.getTemplate(name, "UTF-8");
		}
		
		String str = "";
		Writer out = new StringWriter();
		try{
			template.process(dataModel, out);
			str = out.toString();
		}
		finally{
			out.flush();
			out.close();
		}
		
		return str;
	}
}
