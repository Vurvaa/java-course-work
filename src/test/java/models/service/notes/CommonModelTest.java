package models.service.notes;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommonModelTest {

    @Test
    void testEmptyConstructorAndSetters() {
        CommonModel<String> model = new CommonModel<>();

        UUID uuid = UUID.randomUUID();
        List<String> data = List.of("first", "second");

        model.setUuid(uuid);
        model.setSource("test-api");
        model.setTimeStamp("now");
        model.setData(data);

        assertAll(
                () -> assertEquals(uuid, model.getUUID()),
                () -> assertEquals("test-api", model.getSource()),
                () -> assertEquals("now", model.getTimeStamp()),
                () -> assertEquals(data, model.getData())
        );
    }

    @Test
    void testFullConstructor() {
        UUID uuid = UUID.randomUUID();
        List<Integer> data = List.of(1, 2, 3);

        CommonModel<Integer> model = new CommonModel<>(
                uuid,
                "now",
                "steam-api",
                data
        );

        assertAll(
                () -> assertEquals(uuid, model.getUUID()),
                () -> assertEquals("steam-api", model.getSource()),
                () -> assertEquals("now", model.getTimeStamp()),
                () -> assertEquals(data, model.getData())
        );
    }

    @Test
    void testEmptyModelHasNullFields() {
        CommonModel<String> model = new CommonModel<>();

        assertAll(
                () -> assertNull(model.getUUID()),
                () -> assertNull(model.getSource()),
                () -> assertNull(model.getTimeStamp()),
                () -> assertNull(model.getData())
        );
    }

    @Test
    void testCanSetEmptyData() {
        CommonModel<String> model = new CommonModel<>();

        model.setData(List.of());

        assertTrue(model.getData().isEmpty());
    }
}