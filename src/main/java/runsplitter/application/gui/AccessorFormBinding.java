package runsplitter.application.gui;

import runsplitter.common.Accessor;

/**
 * A {@link FormBinding} that uses {@link Accessor}s for transferring values between bean and form.
 *
 * @param <T> The type of data to bind.
 */
public class AccessorFormBinding<T> implements FormBinding {

    private final Accessor<T> beanAccessor;
    private final Accessor<T> guiAccessor;

    public AccessorFormBinding(Accessor<T> beanAccessor, Accessor<T> guiAccessor) {
        this.beanAccessor = beanAccessor;
        this.guiAccessor = guiAccessor;
    }

    @Override
    public void beanToForm() {
        guiAccessor.set(beanAccessor.get());
    }

    @Override
    public void formToBean() {
        beanAccessor.set(guiAccessor.get());
    }
}
