ALTER TABLE response
    DROP COLUMN request_id,
    ADD COLUMN registry_book_id uuid UNIQUE,
	ADD CONSTRAINT registry_book_response_fk FOREIGN KEY (registry_book_id) REFERENCES registry_book(id);

ALTER TABLE users
    DROP COLUMN pin,
    DROP COLUMN address,
    DROP COLUMN mobile;
	
ALTER TABLE document
	DROP CONSTRAINT document_response_id_fkey,
	ADD CONSTRAINT document_response_id_fkey FOREIGN KEY (response_id) REFERENCES response(id) ON DELETE CASCADE;
	
