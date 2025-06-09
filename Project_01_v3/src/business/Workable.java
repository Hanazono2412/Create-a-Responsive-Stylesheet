
package business;

public interface Workable<T, K> {
    public void addNew(T t);
    public void update(T t);
    public void showAll();
    public T searchById(K id);
}