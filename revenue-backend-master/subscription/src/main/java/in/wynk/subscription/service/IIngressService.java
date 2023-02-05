package in.wynk.subscription.service;

public interface IIngressService<T> {
    void decorate(String ingressIntent, T obj);
}
