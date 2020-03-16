package uniqueconstraint.not.working

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ParentSpec extends Specification implements DomainUnitTest<Parent> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
