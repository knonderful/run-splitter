package runsplitter.application.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for creating {@link FormBinding} instances.
 */
public class FormBindings {

    private FormBindings() {
    }

    /**
     * Creates a {@link FormBinding} from getters and setters from the bean and form.
     *
     * @param <T>        The data type.
     * @param beanGetter The getter on the bean.
     * @param beanSetter The setter on the bean.
     * @param formGetter The getter on the form.
     * @param formSetter The setter on the form.
     * @return The {@link FormBinding}.
     */
    public static <T> FormBinding create(Supplier<T> beanGetter, Consumer<T> beanSetter, Supplier<T> formGetter, Consumer<T> formSetter) {
        return new AccessorFormBinding<>(
                new SimpleAccessor<>(beanGetter, beanSetter),
                new SimpleAccessor<>(formGetter, formSetter)
        );
    }
}
