package pl.proinet.vsj.view.inputs;

import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.function.SerializableFunction;
 
 public class LongField extends AbstractNumberField<LongField, Long> {
 
     private static final SerializableFunction<String, Long> PARSER = valueFormClient -> {
         if (valueFormClient == null || valueFormClient.isEmpty()) {
             return null;
         }
         try {
             return Long.parseLong(valueFormClient);
         } catch (NumberFormatException e) {
             return null;
         }
     };
 
     private static final SerializableFunction<Long, String> FORMATTER = valueFromModel -> valueFromModel == null
             ? ""
             : valueFromModel.toString();
 
     /**
      * Constructs an empty {@code LongField}.
      */
     public LongField() {
         super(PARSER, FORMATTER, Long.MIN_VALUE, Long.MAX_VALUE, true);
     }
 
     /**
      * Constructs an empty {@code LongField} with the given label.
      *
      * @param label
      *            the text to set as the label
      */
     public LongField(String label) {
         this();
         setLabel(label);
     }
 
     /**
      * Constructs an empty {@code LongField} with the given label and
      * placeholder text.
      *
      * @param label
      *            the text to set as the label
      * @param placeholder
      *            the placeholder text to set
      */
     public LongField(String label, String placeholder) {
         this(label);
         setPlaceholder(placeholder);
     }
 
     /**
      * Constructs an empty {@code LongField} with a value change listener.
      *
      * @param listener
      *            the value change listener
      *
      * @see #addValueChangeListener(ValueChangeListener)
      */
     public LongField(
             ValueChangeListener<? super ComponentValueChangeEvent<LongField, Long>> listener) {
         this();
         addValueChangeListener(listener);
     }
 
     /**
      * Constructs an empty {@code LongField} with a value change listener and
      * a label.
      *
      * @param label
      *            the text to set as the label
      * @param listener
      *            the value change listener
      *
      * @see #setLabel(String)
      * @see #addValueChangeListener(ValueChangeListener)
      */
     public LongField(String label,
             ValueChangeListener<? super ComponentValueChangeEvent<LongField, Long>> listener) {
         this(label);
         addValueChangeListener(listener);
     }
 
     /**
      * Constructs a {@code LongField} with a value change listener, a label
      * and an initial value.
      *
      * @param label
      *            the text to set as the label
      * @param initialValue
      *            the initial value
      * @param listener
      *            the value change listener
      *
      * @see #setLabel(String)
      * @see #setValue(Object)
      * @see #addValueChangeListener(ValueChangeListener)
      */
     public LongField(String label, Long initialValue,
             ValueChangeListener<? super ComponentValueChangeEvent<LongField, Long>> listener) {
         this(label);
         setValue(initialValue);
         addValueChangeListener(listener);
     }
 
     /**
      * Sets the minimum value of the field. Entering a value which is smaller
      * than {@code min} invalidates the field.
      *
      * @param min
      *            the min value to set
      */
     public void setMin(int min) {
         super.setMin(min);
     }
 
     /**
      * Gets the minimum allowed value of the field.
      *
      * @return the min property of the field
      * @see #setMin(int)
      */
     public int getMin() {
         return (int) getMinDouble();
     }
 
     /**
      * Sets the maximum value of the field. Entering a value which is greater
      * than {@code max} invalidates the field.
      *
      * @param max
      *            the max value to set
      */
     public void setMax(int max) {
         super.setMax(max);
     }
 
     /**
      * Gets the maximum allowed value of the field.
      *
      * @return the max property of the field
      * @see #setMax(int)
      */
     public int getMax() {
         return (int) getMaxDouble();
     }
 
     /**
      * Sets the allowed number intervals of the field. This specifies how much
      * the value will be increased/decreased when clicking on the
      * {@link #setStepButtonsVisible(boolean) step buttons}. It is also used to
      * invalidate the field, if the value doesn't align with the specified step
      * and {@link #setMin(int) min} (if specified by user).
      *
      * @param step
      *            the new step to set
      * @throws IllegalArgumentException
      *             if the argument is less or equal to zero.
      */
     public void setStep(int step) {
         if (step <= 0) {
             throw new IllegalArgumentException(
                     "The step cannot be less or equal to zero.");
         }
         super.setStep(step);
     }
 
     /**
      * Gets the allowed number intervals of the field.
      *
      * @return the step property of the field
      * @see #setStep(int)
      */
     public int getStep() {
         return (int) getStepDouble();
     }
 }
 