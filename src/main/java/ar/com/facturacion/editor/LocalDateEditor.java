package ar.com.facturacion.editor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException{
        System.out.println(text);
        setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Override
    public String getAsText() throws IllegalArgumentException {
        System.out.println(getValue());
        if (getValue() != null) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format((LocalDate) getValue());
        }
        return (String) getValue();
    }
}
