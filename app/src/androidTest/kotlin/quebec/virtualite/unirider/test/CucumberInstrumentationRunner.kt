package quebec.virtualite.unirider.test

import android.os.Bundle
import cucumber.api.CucumberOptions
import cucumber.api.SnippetType.CAMELCASE
import quebec.virtualite.commons.android.BaseCucumberInstrumentationRunner

@CucumberOptions
    (
    features = ["features"],
    glue = ["quebec.virtualite.unirider.test"],
    monochrome = true,
    snippets = CAMELCASE,
    strict = true,
    tags = [
//        "@WIP",
        "~@Ignore"]
)
@Suppress("unused")
class CucumberInstrumentationRunner : BaseCucumberInstrumentationRunner()
{
    override fun onCreate(arguments: Bundle)
    {
        if (!arguments.containsKey("tags"))
        {
            arguments.putString("tags", BuildConfig.SCENARIOS)
        }

        super.onCreate(arguments)
    }
}
