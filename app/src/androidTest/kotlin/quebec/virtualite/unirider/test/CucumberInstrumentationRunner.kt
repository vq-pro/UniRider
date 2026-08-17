package quebec.virtualite.unirider.test

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
class CucumberInstrumentationRunner : BaseCucumberInstrumentationRunner()
