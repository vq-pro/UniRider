package quebec.virtualite.unirider.services.client

import quebec.virtualite.commons.android.rest.BaseBackendClient
import quebec.virtualite.commons.android.rest.BasicAuthentication
import quebec.virtualite.unirider.BuildConfig.SERVER_BASE_URL
import quebec.virtualite.unirider.services.GreetingResponse
import quebec.virtualite.unirider.services.SimulatedBackend

const val TEST_USER = "joe_user"
const val TEST_PASSWORD = "123456"

class SimulatedBackendClient : SimulatedBackend, BaseBackendClient() {
    override fun greet(name: String?, onSuccess: ((GreetingResponse) -> Unit)?) {
        val token = BasicAuthentication.token(TEST_USER, TEST_PASSWORD)

        api(SERVER_BASE_URL, SimulatedBackendApi::class.java)
            .greet(token, name!!)
            .enqueue(request(onSuccess!!))
    }
}