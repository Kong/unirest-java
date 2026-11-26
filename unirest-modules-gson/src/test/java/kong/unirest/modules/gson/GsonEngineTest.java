package kong.unirest.modules.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kong.unirest.core.json.JsonEngine;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import static kong.unirest.modules.gson.GsonEngineTest.GsonArrayAssert.assertThatArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class GsonEngineTest {

    GsonEngine engine = new GsonEngine();

    @Test
    void numbersInArrays() {
        var array = engine.newJsonArray("[1,2,3]");

        assertThatArray(array)
                .hasSize(3)
                .containsExactly(1, 2, 3);
    }
    

    public static class GsonArrayAssert
            extends AbstractAssert<GsonArrayAssert, JsonEngine.Array> {

        private GsonArrayAssert(JsonEngine.Array actual) {
            super(actual, GsonArrayAssert.class);
        }

        public static GsonArrayAssert assertThatArray(JsonEngine.Array actual) {
            return new GsonArrayAssert(actual);
        }

        public GsonArrayAssert hasSize(int expectedSize) {
            assertThat(actual.size())
                    .isEqualTo(expectedSize);
            return this;
        }

        public GsonArrayAssert equalsAtPos(int position, int expected) {

            try {
                var engineElement = actual.get(position).getEngineElement();
                assertThat(engineElement)
                        .as("failure at position %s", position)
                        .isEqualTo(new JsonPrimitive(expected));
            }catch (IndexOutOfBoundsException i ){
                fail("number (%s) at position (%s) is greater than the size of the array (%s)",
                        expected, position + 1, actual.size());
            }
            return this;
        }

        public void containsExactly(int... expected) {
            int p = 0;
            for(int i : expected){
                equalsAtPos(p, i);
                p++;
            }
            assertThat(p)
                    .as("Array contains more elements than was checked!")
                    .isEqualTo(actual.size());
        }
    }
}