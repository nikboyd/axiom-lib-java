package org.axiom_tools.storage;

import java.util.Optional;
import org.axiom_tools.domain.PhoneNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author nik
 */
public interface PhoneStorage extends Repository<PhoneNumber, Long> {

    @Query("SELECT count(p) FROM PhoneNumber p")
    int countAll();

    @Query("SELECT p FROM PhoneNumber p WHERE p.key = :id")
    Optional<PhoneNumber> findID(@Param("id") Long id);

    PhoneNumber save(PhoneNumber model);

    void delete(PhoneNumber model);

} // PhoneStorage
