package BehaviorTests;

import com.google.common.base.MoreObjects;

public class Foo {
    public String bar;

    public Foo(){ }

    public Foo(String bar) {
        this.bar = bar;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("bar",bar).toString();
    }
}
