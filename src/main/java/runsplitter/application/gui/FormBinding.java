package runsplitter.application.gui;

/**
 * An interface for transferring value between a backing bean and a (GUI) form.
 * <p>
 * This approach can be used for bridging bean field and form controls (such as a text input field) in a generic way
 * without the need for writing much boiler-plate code.
 */
public interface FormBinding {

    /**
     * Transfers data from the bean to the form.
     */
    void beanToForm();

    /**
     * Transfers data from the form to the bean.
     */
    void formToBean();
}
