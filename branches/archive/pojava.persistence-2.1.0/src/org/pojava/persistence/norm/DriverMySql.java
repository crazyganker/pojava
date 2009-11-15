package org.pojava.persistence.norm;

public class DriverMySql {
    /*
     * MySql uses autonumbers, not sequences.  One might be able to
     * simulate a sequence, though.
     */
    
    
/*
create table sequence (
name varchar(64) primary key,
last_val int unsigned not null
) engine=myisam;

INSERT INTO sequence (name, last_val) VALUES ('seq', 0);
"""

now when you want to get the next number in the sequence do:
"""
UPDATE sequence SET last_val=@val:=last_val+1 WHERE name='table_changes'; 
 */
}
