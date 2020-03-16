package uniqueconstraint.not.working

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ChildSpec extends Specification implements DomainUnitTest<Child> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
