package sk.insomnia.rowingRace.dao;

import sk.insomnia.rowingRace.so.Address;

import java.sql.Connection;
import java.sql.SQLException;

public interface AddressDao {

    public void saveOrUpdate(Address address) throws SQLException;

    public Address getById(Long id) throws SQLException;

    public Address getById(Long id, Connection connection) throws SQLException;
}
