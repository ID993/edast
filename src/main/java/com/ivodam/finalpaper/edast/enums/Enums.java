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

    public enum RequestType {
        BDM("bdm"),
        WORK("Work"),
        SCHOOL("School"),
        CADASTRAL("Cadastral"),
        OTHER("Other");

        public final String label;

        RequestType(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return label;
        }

    }

    public enum BDMSelection {
        BIRTH("Birth"),
        DEATH("Death"),
        MARRIAGE("Marriage");

        public final String label;

        BDMSelection(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return label;
        }

    }
}
