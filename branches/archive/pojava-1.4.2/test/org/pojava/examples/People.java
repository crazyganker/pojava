package org.pojava.examples;

import java.util.ArrayList;
import java.util.List;

public class People {

    List people = new ArrayList();
    Person leader = null;

    public List getPeople() {
        return people;
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public Person getLeader() {
        return leader;
    }

    public void setLeader(Person leader) {
        this.leader = leader;
    }

}
