package quebec.virtualite.unirider.services.client

import quebec.virtualite.unirider.services.GreetingResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SimulatedBackendApi
{
    @GET("/v2/greetings/{name}")
    fun greet(@Header("Authorization") authKey: String, @Path("name") name: String): Call<GreetingResponse>
}
