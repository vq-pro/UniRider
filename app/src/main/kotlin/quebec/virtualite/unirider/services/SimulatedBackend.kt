package quebec.virtualite.unirider.services

interface SimulatedBackend
{
    fun greet(name: String?, onSuccess: ((GreetingResponse) -> Unit)?)
}