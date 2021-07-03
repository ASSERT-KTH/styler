package com.java110.web.core;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Date;

public class VcCreateProcessor extends AbstractElementTagProcessor {


    private static Logger logger = LoggerFactory.getLogger(VcCreateProcessor.class);

    private static final String DIV_PROPERTY_COMPONENT = "data-component";

    private static final String TAG_NAME = "create";
    private static final String NAME = "name";
    private static final int PRECEDENCE = 300;


    public VcCreateProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {

        //获取模板名称
        String componentName = tag.getAttributeValue(NAME);

        logger.debug("正在解析组件{},{}", componentName, new Date().getTime());
        String html = VueComponentTemplate.findTemplateByComponentCode(componentName + "." + VueComponentTemplate.COMPONENT_HTML);
        if (html == null) {
            throw new RuntimeException("在缓存中未找到组件【" + componentName + "】");
        }

        IModelFactory modelFactory = context.getModelFactory();

        IModel htmlModel = modelFactory.createModel();

        Document doc = Jsoup.parseBodyFragment(html);

        //获取我们真实的一个元素，去除因为转换而加入的 html 和body 标签
        Elements elements = doc.body().children();

        //将组建名称写入组建HTML 第一个标签中
        addDataComponent(elements, componentName);

        htmlModel.addModel(modelFactory.parse(context.getTemplateData(), doc.body().children().toString()));

        String css = VueComponentTemplate.findTemplateByComponentCode(componentName + "." + VueComponentTemplate.COMPONENT_CSS);
        if (css != null) {
            css = "<style type=\"text/css\">" + css + "</style>";
            htmlModel.add(modelFactory.createText(css));
        }

        //js
        String js = VueComponentTemplate.findTemplateByComponentCode(componentName + "." + VueComponentTemplate.COMPONENT_JS);
        if (js != null) {

            js = dealJs(js, tag);
            js = dealJsAddComponentCode(js, tag);
            js = "<script type=\"text/javascript\">//<![CDATA[ \n" + js + "//]]>\n</script>";
            htmlModel.add(modelFactory.createText(js));

        }

        logger.debug("解析完成组件{},{}", componentName, new Date().getTime());
        structureHandler.replaceWith(htmlModel, true);

    }


    /**
     * 加入组件名称到 HTML中 方便定位问题
     *
     * @param elements      页面节点
     * @param componentCode 组件编码
     */
    private void addDataComponent(Elements elements, String componentCode) {
        Element tmpElement = elements.get(0);
        tmpElement.attr(DIV_PROPERTY_COMPONENT, componentCode);
    }


    /**
     * 处理js
     *
     * @param tag 页面元素
     * @param js  js文件内容
     * @return js 文件内容
     */
    private String dealJs(String js, IProcessableElementTag tag) {

        //在js 中检测propTypes 属性
        if (!js.contains("propTypes")) {
            return js;
        }

        //解析propTypes信息
        String tmpProTypes = js.substring(js.indexOf("propTypes"));
        tmpProTypes = tmpProTypes.substring(tmpProTypes.indexOf("{") + 1, tmpProTypes.indexOf("}")).trim();

        if (StringUtils.isEmpty(tmpProTypes)) {
            return js;
        }

        tmpProTypes = tmpProTypes.contains("\r")? tmpProTypes.replace("\r", "") : tmpProTypes;

        String[] tmpType = tmpProTypes.contains("\n")
                        ? tmpProTypes.split("\n")
                        : tmpProTypes.split(",");
        StringBuffer propsJs = new StringBuffer("\nvar $props = {};\n");
        for (String type : tmpType) {
            if (StringUtils.isEmpty(type) || !type.contains(":")) {
                continue;
            }
            String[] types = type.split(":");
            String attrKey = "";
            if (types[0].contains("//")) {
                attrKey = types[0].substring(0, types[0].indexOf("//"));
            }
            attrKey = types[0].replace(" ", "")
                    .replace("\n", "")
                    .replace("\r", "");
            if (!tag.hasAttribute(attrKey)) {
                String componentName = tag.getAttributeValue("name");
                logger.error("组件" + componentName + "未配置组件属性 " + attrKey);
                throw new TemplateProcessingException("组件[" + componentName + "]未配置组件属性" + attrKey);
            }
            String vcType = tag.getAttributeValue(attrKey);
            if (types[1].contains("vc.propTypes.string")) {
                vcType = "'" + vcType + "'";
            }
            propsJs.append("$props." + attrKey + "=" + vcType + ";\n");
        }

        //将propsJs 插入到 第一个 { 之后
        int position = js.indexOf("{");
        if (position < 0) {
            String componentName = tag.getAttributeValue("name");
            logger.error("组件" + componentName + "对应js 未包含 {}  ");
            throw new TemplateProcessingException("组件" + componentName + "对应js 未包含 {}  ");
        }
        js = new StringBuffer(js).insert(position + 1, propsJs).toString();
        return js;
    }

    /**
     * 处理js 变量和 方法都加入 组件编码
     *
     * @param tag 页面元素
     * @param js  js文件内容
     * @return js 文件内容
     */
    private String dealJsAddComponentCode(String js, IProcessableElementTag tag) {

        if (!tag.hasAttribute("code")) {
            return js;
        }

        String code = tag.getAttributeValue("code");

        return js.replace("@vc_", code);
    }
}
