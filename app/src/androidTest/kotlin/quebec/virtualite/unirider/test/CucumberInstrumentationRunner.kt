package quebec.virtualite.unirider.test

import cucumber.api.CucumberOptions
import cucumber.api.SnippetType.CAMELCASE
import quebec.virtualite.commons.android.BaseCucumberInstrumentationRunner
import quebec.virtualite.unirider.BuildConfig

@CucumberOptions
    (
    features = ["features"],
    glue = ["quebec.virtualite.unirider.test"],
    monochrome = true,
    snippets = CAMELCASE,
    strict = true,
    tags = ["~@Ignore", BuildConfig.SCENARIOS]
)
class CucumberInstrumentationRunner : BaseCucumberInstrumentationRunner()
