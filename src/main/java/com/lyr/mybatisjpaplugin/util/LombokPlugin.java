package com.lyr.mybatisjpaplugin.util;

/**
 */
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.*;

/** 自定义的lombok注解配置
 */
public class LombokPlugin extends PluginAdapter {

    private final Collection<Annotations> annotations;

    /**
     * LombokPlugin constructor
     */
    public LombokPlugin() {
        annotations = new LinkedHashSet<>(Annotations.values().length);
    }

    /**
     * @param warnings list of warnings
     * @return always true
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * Intercepts base record class generation 获取表
     *
     * @param topLevelClass     the generated base record class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addAnnotations(topLevelClass);
        return true;
    }

    /**
     * Intercepts primary key class generation
     *
     * @param topLevelClass     the generated primary key class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true sfs
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addAnnotations(topLevelClass);
        return true;
    }

    /**
     * Intercepts "record with blob" class generation
     *
     * @param topLevelClass     the generated record with BLOBs class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addAnnotations(topLevelClass);
        return true;
    }

    /**
     * 设置get set方法(使用lombok不需要,直接返回false)
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * 设置set方法
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * 设置lombok注解 <br>
     */
    private void addAnnotations(TopLevelClass topLevelClass) {
        for (Annotations annotation : annotations) {
            topLevelClass.addImportedType(annotation.javaType);
            topLevelClass.addAnnotation(annotation.asAnnotation());
        }
    }

    public void importAnnotationOnClass() {

    }
    public void importAnnotationOnId() {

    }

    /**
     * entity类设置
     * @param properties sfsffsf
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        //@Data is default annotation
        annotations.add(Annotations.DATA);
        annotations.add(Annotations.ALL_ARGS_CONSTRUCTOR);
        annotations.add(Annotations.NO_ARGS_CONSTRUCTOR);
        annotations.add(Annotations.BUILDER);
        importAnnotationOnClass();
        //自动插入 import com.demo.tableId
        //TODO: 这里待修改

        // annotations.add(Annotations.Table_id);
        for (String annotationName : properties.stringPropertyNames()) {
            if (annotationName.contains(".")) {
                continue;
            }
            String value = properties.getProperty(annotationName);
            if (!Boolean.parseBoolean(value)) {
                // The annotation is disabled, skip it
                continue;
            }
            Annotations annotation = Annotations.getValueOf(annotationName);
            if (annotation == null) {
                continue;
            }
            String optionsPrefix = annotationName + ".";
            for (String propertyName : properties.stringPropertyNames()) {
                if (!propertyName.startsWith(optionsPrefix)) {
                    // A property not related to this annotation
                    continue;
                }
                String propertyValue = properties.getProperty(propertyName);
                annotation.appendOptions(propertyName, propertyValue);
                annotations.add(annotation);
                annotations.addAll(Annotations.getDependencies(annotation));
            }
        }
    }

    /**
     * mapper类设置注解
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        return true;
    }

    /**
     * entity字段设置
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType) {
        // if (field.getType().getShortNameWithoutTypeArguments().equals("Date")) {
        //     // field.getAnnotations().add(Annotations.DATE_TIME_FORMAT.asAnnotation());
        //     // field.getAnnotations().add(Annotations.JSON_FORMAT.asAnnotation());
        //     // topLevelClass.addImportedType(Annotations.DATE_TIME_FORMAT.javaType);
        //     // topLevelClass.addImportedType(Annotations.JSON_FORMAT.javaType);
        //
        // }
        //列名
        String columnName = field.getName().toLowerCase();
        //注释名字
        String remark = introspectedColumn.getRemarks().toLowerCase();
        if (field.getType().getShortNameWithoutTypeArguments().equals("Date")) {
            //如果是时间类型

            if (columnName.contains("create")) {
                field.getAnnotations().add(Annotations.Creation_time.asAnnotation());
                topLevelClass.addImportedType(Annotations.Creation_time.javaType);
            }else if (columnName.contains("update") || columnName.contains("modif")) {
                field.getAnnotations().add(Annotations.Update_time.asAnnotation());
                topLevelClass.addImportedType(Annotations.Update_time.javaType);
            }
        }

        if (introspectedColumn.isIdentity() || remark.contains("主键")||remark.contains("key") || remark.contains("pk")) {
            //如果是 id 主键 的话
            // field.getAnnotations().add(Annotations.Table_id.asAnnotation());
            // topLevelClass.addImportedType(Annotations.Table_id.javaType);
            importAnnotationOnId();

        }
        System.out.println(columnName);
        //判断是不是逻辑删除字段，隐藏起来
        if (columnName.contains("delete")) {
            field.getAnnotations().add(Annotations.Json_ignore.asAnnotation());
            topLevelClass.addImportedType(Annotations.Json_ignore.javaType);
        }
        // System.out.println(columnName.contains("notnull"));
        // @NotNull
        if (remark.contains("@notnull")) {
            //not null
            importAnnotation(Annotations.Not_null,field,topLevelClass);
        }
        if (remark.contains("@notblank")) {
            importAnnotation(Annotations.Not_blank,field,topLevelClass);
        }
        if(remark.contains("@email")) {
            importAnnotation(Annotations.Email,field,topLevelClass);
        }
        if (columnName.equalsIgnoreCase("version")) {
            importAnnotation(Annotations.Version,field,topLevelClass);
            //乐观锁不是给前端看的，数据库层面的，因此需要隐藏
            importAnnotation(Annotations.Json_ignore,field,topLevelClass);
        }
        if(columnName.contains("salt") || columnName.contains("password")|| remark.contains("@ignore")) {
            //盐
            importAnnotation(Annotations.Json_ignore,field,topLevelClass);
        }

        //数据库默认值
        // if (introspectedColumn.)
        return true;
    }

    private void importAnnotation(Annotations mark,Field field,TopLevelClass topLevelClass ) {
        field.getAnnotations().add(mark.asAnnotation());
        topLevelClass.addImportedType(mark.javaType);
    }

    public enum Annotations {
        TableId("","@Id","javax.persistence.Id"),
        // GeneratedValue("","",""),

        DATA("data", "@Data", "lombok.Data"),
        BUILDER("builder", "@Builder", "lombok.Builder"),
        ALL_ARGS_CONSTRUCTOR("allArgsConstructor", "@AllArgsConstructor", "lombok.AllArgsConstructor"),
        NO_ARGS_CONSTRUCTOR("noArgsConstructor", "@NoArgsConstructor", "lombok.NoArgsConstructor"),
        ACCESSORS("accessors", "@Accessors", "lombok.experimental.Accessors"),
        TO_STRING("toString", "@ToString", "lombok.ToString"),
        DATE_TIME_FORMAT("dateTimeFormat", "@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")", "org.springframework.format.annotation.DateTimeFormat"),
        JSON_FORMAT("jsonFormat", "@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")", "com.fasterxml.jackson.annotation.JsonFormat"),
        Json_ignore("","@JsonIgnore","com.fasterxml.jackson.annotation.JsonIgnore"),

        /*
        *
        *
        *
        *
        * */

        Creation_time("","@CreateTime","com.lyr.mybatisjpaplugin.annotation.CreateTime"),
        Update_time("","@UpdateTime","com.lyr.mybatisjpaplugin.annotation.UpdateTime"),

        Version("","@Version","javax.persistence.Version"),

        Not_null("","@NotNull","javax.validation.constraints.NotNull"),
        Not_blank("","@NotBlank","javax.validation.constraints.NotBlank"),
        Email ("","@Email","javax.validation.constraints.Email")

        ;
        private final String paramName;
        private final String name;
        private final FullyQualifiedJavaType javaType;
        private final List<String> options;

        Annotations(String paramName, String name, String className) {
            this.paramName = paramName;
            this.name = name;
            this.javaType = new FullyQualifiedJavaType(className);
            this.options = new ArrayList<String>();
        }

        private static Annotations getValueOf(String paramName) {
            for (Annotations annotation : Annotations.values()) {
                if (String.CASE_INSENSITIVE_ORDER.compare(paramName, annotation.paramName) == 0) {
                    return annotation;
                }
            }

            return null;
        }

        private static Collection<Annotations> getDependencies(Annotations annotation) {
            if (annotation == ALL_ARGS_CONSTRUCTOR) {
                return Collections.singleton(NO_ARGS_CONSTRUCTOR);
            } else {
                return Collections.emptyList();
            }
        }

        // A trivial quoting.
        // Because Lombok annotation options type is almost String or boolean.
        private static String quote(String value) {
            if (Boolean.TRUE.toString().equals(value) || Boolean.FALSE.toString().equals(value))
            // case of boolean, not passed as an array.
            {
                return value;
            }
            return value.replaceAll("[\\\\w]+", "\"$0\"");
        }

        private void appendOptions(String key, String value) {
            String keyPart = key.substring(key.indexOf(".") + 1);
            String valuePart = value.contains(",") ? String.format("{%s}", value) : value;
            this.options.add(String.format("%s=%s", keyPart, quote(valuePart)));
        }

        private String asAnnotation() {
            if (options.isEmpty()) {
                return name;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");
            boolean first = true;
            for (String option : options) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(option);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
