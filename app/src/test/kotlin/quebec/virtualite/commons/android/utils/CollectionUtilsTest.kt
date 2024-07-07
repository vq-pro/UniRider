package quebec.virtualite.commons.android.utils

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM_SERIALIZED

@RunWith(MockitoJUnitRunner::class)
class CollectionUtilsTest {
    @Test
    fun deserialize() {
        // When
        val resultat = CollectionUtils.deserialize(WHS_PER_KM_SERIALIZED)

        // Then
        assertThat(resultat, equalTo(WHS_PER_KM))
    }

    @Test
    fun serialize() {
        // When
        val resultat = CollectionUtils.serialize(WHS_PER_KM)

        // Then
        assertThat(resultat, equalTo(WHS_PER_KM_SERIALIZED))
    }
}
