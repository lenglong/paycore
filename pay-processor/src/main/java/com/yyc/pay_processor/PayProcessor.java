package com.yyc.pay_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yyc.pay_annotation.WXPayEntry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2019-06-17
 * ==========================
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.yyc.pay_annotation.WXPayEntry")
public class PayProcessor extends AbstractProcessor {

    Elements elementUtils;
    Types typeUtils;
    Messager messager;
    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(set.isEmpty()){
            return false;
        }
        //生成一个 Class xxx.wxapi.WXEntryActivity extends BaseWXEntryActivity
        generateWXPayCode(roundEnvironment);
        return true;
    }

    private void generateWXPayCode(RoundEnvironment roundEnvironment) {
//        WXEntryElementVisitor visitor = new WXEntryElementVisitor();
//        visitor.setFiler(mFiler);
//        scanElement(roundEnvironment, WXPayEntry.class,visitor);

        TypeElement applicationElement = elementUtils.getTypeElement("android.app.Application");
        TypeMirror applicationType = applicationElement.asType();

        Set<? extends Element> elementsAnnotatedWithWXPayEntry = roundEnvironment.getElementsAnnotatedWith(WXPayEntry.class);
        for (Element element : elementsAnnotatedWithWXPayEntry) {

//            List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
//            for (AnnotationMirror annotationMirror : annotationMirrors) {
//                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
//                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
//                    entry.getValue().accept(visitor,null);
//                }
//            }

            TypeMirror elementType = element.asType();
            if(typeUtils.isSubtype(elementType,applicationType)){
                createClass(element.getAnnotation(WXPayEntry.class));
                break;
            }
        }

    }

    private void createClass(WXPayEntry wxPayEntry){
        TypeElement baseWxEntryActivityEle = elementUtils.getTypeElement("com.yyc.paycore.BaseWXEntryActivity");

        TypeSpec.Builder classTypeSpec = TypeSpec.classBuilder("WXPayEntryActivity")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(baseWxEntryActivityEle.asType()));
        try {
            JavaFile.builder(wxPayEntry.packageName()+".wxapi",classTypeSpec.build())
                    .addFileComment("微信支付自动生成")
                    .build().writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE,"微信支付自动生成代码报错啦");
        }
    }
}
