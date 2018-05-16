package runsplitter.application.gui;

import java.util.Collection;
import java.util.HashSet;

/**
 * A {@link MultiFormBinding} that combines multiple {@link MultiFormBinding}s into one and delegates all calls.
 */
public class MultiFormBinding implements FormBinding {

    private final Collection<FormBinding> adapters = new HashSet<>(4);

    public void add(FormBinding e) {
        adapters.add(e);
    }

    @Override
    public void beanToForm() {
        adapters.forEach(FormBinding::beanToForm);
    }

    @Override
    public void formToBean() {
        adapters.forEach(FormBinding::formToBean);
    }
}
