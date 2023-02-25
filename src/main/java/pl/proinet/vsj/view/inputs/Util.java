package pl.proinet.vsj.view.inputs;

import java.util.Optional;

import javax.persistence.metamodel.Attribute;



public class Util {
    
    static <T extends java.lang.annotation.Annotation> Optional<T> annotationOf(Attribute<?, ?> attr, Class<T> annotation) {
        Class<?> declaringClass = attr.getJavaMember().getDeclaringClass();
        while( declaringClass != null) {
            try {
                return Optional.of(declaringClass.getDeclaredField(attr.getName()).getAnnotation(annotation));
            } catch (NoSuchFieldException e) {
                try {
                    return Optional.of(declaringClass.getDeclaredMethod( toGetter(attr.getName())).getAnnotation(annotation));
                } catch (Exception e1) {
                }
            } 
            declaringClass = declaringClass.getSuperclass();
            if( declaringClass.equals(Object.class))
                return Optional.empty();
        }
        return Optional.empty();
    }


    static String toGetter(String name) {
        return "get" + name.substring(0,1).toUpperCase() + name.substring(1);
    }

}
