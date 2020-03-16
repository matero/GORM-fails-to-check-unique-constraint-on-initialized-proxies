package uniqueconstraint.not.working

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.*
import org.grails.datastore.mapping.proxy.EntityProxy
import spock.lang.Specification

@Integration
@Rollback
class FailureOfUniqueConstraintSpec extends Specification {

    def "modified domains object works as expected"() {
        given: "I have a Domain OBJECT"
        final Child object = new Child(name: "object").save(failOnError: true)
        assert !(object instanceof EntityProxy)
        and: "I have another Domain OBJECT with the same name"
        final Child another = new Child(name: "object")
        assert !(object instanceof EntityProxy)

        when: "I try to validate the another object"
        another.validate()

        then: "another should have an error on name because it is duplicated"
        another.hasErrors()
        another.errors.hasFieldErrors("name")
        another.errors.getFieldError("name").codes.contains("unique.name")

        cleanup:
        object?.delete(flush: true)
    }

    def "modified child domains object works as expected"() {
        given: "I have a Domain OBJECT"
        final Child object = new Child(name: "object").save(failOnError: true)
        assert !(object instanceof EntityProxy)
        final Parent parent = new Parent(child: object).save(failOnError: true)
        assert !(parent instanceof EntityProxy)

        and: "I have another Domain OBJECT with the same name"
        final Child anotherChild = new Child(name: "object")
        assert !(object instanceof EntityProxy)
        final Parent anotherParent = new Parent(child: anotherChild)
        assert !(parent instanceof EntityProxy)

        when: "I try to validate the another object"
        anotherParent.validate()

        then: "another should have an error on name because it is duplicated"
        anotherParent.hasErrors()
        anotherParent.errors.hasFieldErrors("child.name")
        anotherParent.errors.getFieldError("child.name").codes.contains("unique.name")

        cleanup:
        object?.delete(flush: true)
        parent?.delete(flush: true)
    }

    def "unmodified child proxies object fails unique constraint checking"() {
        given: "I have a Domain OBJECT"
        Long childId, parentId
        Parent.withNewSession {
            Parent.withNewTransaction {
                final Child object = new Child(name: "object").save(failOnError: true)
                final Parent parent = new Parent(child: object).save(failOnError: true)

                childId = object.id
                parentId = parent.id
            }
        }
        and:
        int tries = 20
        while (!Parent.exists(parentId) && !Child.exists(childId) && tries-- > 0) {
            sleep(50)
        }

        and: "I access the parent, forcing the child to be initialized"

        def parent = Parent.findAll()[0]
        assert parent.child instanceof EntityProxy
        parent.child.name == "object"

        when: "I try to validate the the parent (which then tries to validate the child)"
        parent.validate()

        then: "parent.child should not have any errors!"
        !parent.hasErrors()
        !parent.errors.hasFieldErrors("child.name")
        !parent.errors.getFieldError("child.name").codes.contains("unique.name")

        cleanup:

        Parent.withNewSession {
            Parent.withNewTransaction {
                Child.get(childId)?.delete(flush: true)
                Parent.get(parentId)?.delete(flush: true)
            }
        }
        tries = 20
        while (Parent.exists(parentId) && Child.exists(childId) && tries-- > 0) {
            sleep(50)
        }
    }
}
