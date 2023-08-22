package com.ivodam.finalpaper.edast.enums;

public class Enums {

    public enum Roles {
        ROLE_ADMIN("Admin"),
        ROLE_USER("User"),

        ROLE_EMPLOYEE("Employee");

        public final String label;

        Roles(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return label;
        }

    }

    public enum JobPosition {
        DIRECTOR("Director"),
        ARCHIVAL_ADVISOR("Archival Advisor"),
        SENIOR_ARCHIVIST("Senior Archivist"),
        SPECIALIZED_ARCHIVIST("Specialized Archivist"),
        ARCHIVIST("Archivist"),
        ARCHIVE_TECHNICIAN("Archive Technician"),
        ACCOUNTANT("Accountant"),
        ADMINISTRATIVE_CLERK("Administrative Clerk"),
        CARETAKER("Caretaker"),
        CLEANER("Cleaner");

        public final String label;
        JobPosition(String label) {
            this.label = label;
        }
        public String getDisplayName() {
            return label;
        }

    }

}
