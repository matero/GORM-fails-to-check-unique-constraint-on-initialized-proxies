package uniqueconstraint.not.working

class Parent {

    static mapWith = "neo4j"

    Child child

    static constraints = {
        child nullable: false
    }

    static mapping = {
        child lazy: false
        id generator: 'snowflake'
    }
}
