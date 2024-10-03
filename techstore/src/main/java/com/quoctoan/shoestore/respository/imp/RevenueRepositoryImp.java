package com.quoctoan.shoestore.respository.imp;

import com.quoctoan.shoestore.entity.Product;
import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.respository.IRevenueRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class RevenueRepositoryImp implements IRevenueRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Double getRevenueFromProduct() {
        Double res = 0.0;
        StringBuilder hql = new StringBuilder("Select SUM(O.totalPrice) from Order O " +
                "WHERE O.orderStatus.id = 5 OR O.orderStatus.id = 4");
        try {
            Session session = sessionFactory.openSession();
            Query query = session.createQuery(hql.toString());
            res = (Double) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (res == null) {
            return 0.0;
        }
        return res;
    }

}
