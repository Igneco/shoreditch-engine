package im.mange.shoreditch.engine.model

case class TestPackRunReport(testPack: TestPack, testRunReports: List[TestRunReport]) {
  def successful = testRunReports.filterNot(_.successful).isEmpty
}