package uniqueconstraint.not.working

class Child {

    static mapWith = "neo4j"

    String name

    static constraints = {
        name nullable: false, unique: true
    }
}
