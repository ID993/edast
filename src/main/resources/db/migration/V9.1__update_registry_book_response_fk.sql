ALTER TABLE response
    DROP CONSTRAINT response_user_id_fkey,
    DROP CONSTRAINT registry_book_response_fk;

ALTER TABLE response
    ADD CONSTRAINT response_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    ADD CONSTRAINT registry_book_response_fk FOREIGN KEY (registry_book_id) REFERENCES registry_book(id) ON DELETE CASCADE;

