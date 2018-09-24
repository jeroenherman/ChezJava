package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Beverage;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DAOBeverageMySqlTest {
    DAOBeverageMySql daoBeverageMySql;

    @Before
    public void setUp() throws Exception {
        daoBeverageMySql = DAOBeverageMySql.getInstance();
    }
    @Test
    public void getInstance() throws Exception {
        assertTrue("only one instance possible",daoBeverageMySql  == DAOBeverageMySql.getInstance());
    }

    @Test
    public void getBeverageByID() throws Exception {
        assertEquals("Beverage 1 is not Cola",DAOBeverageMySql.getInstance().getBeverage(1).getItemName(),"Cola");
        assertTrue("Beverage is not found",DAOBeverageMySql.getInstance().getBeverage(0).getItemName().equalsIgnoreCase("NotFound"));
    }

    @Test
    public void getBeverageByName() throws Exception {
        assertEquals("Beverage 1 is not Cola",DAOBeverageMySql.getInstance().getBeverage("Cola").getItemID(),1);
        assertEquals("Beverage 0 is not found",DAOBeverageMySql.getInstance().getBeverage("fluitjesbier").getItemID(),0);
        assertTrue("Beverage is not found",DAOBeverageMySql.getInstance().getBeverage("fluitjesbier").getItemName().equalsIgnoreCase("NotFound"));

    }

    @Test
    public void getAllBeverages() throws Exception {
        List<Beverage> beverages = DAOBeverageMySql.getInstance().getAllBeverages();
        assertFalse("beverages is null", beverages == null);

    }

}