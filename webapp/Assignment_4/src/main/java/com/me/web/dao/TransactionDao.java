package com.me.web.dao;

import com.me.web.pojo.Attachment;
import com.me.web.pojo.Transaction;
import com.me.web.pojo.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TransactionDao extends DAO{

    public TransactionDao() throws IOException {
        super();
    }
    public int insertTransaction(Transaction transaction) throws Exception{
       try{
           begin();
           getSession().save(transaction);
           getSession().flush();
           commit();
           getSession().clear();
           return 2;
       } catch(HibernateException e){
           rollback();
           throw new Exception("Transaction not saved: "+e.getMessage());
        }

    }

    public Transaction getTransactionById(UUID id) throws Exception{
        try {
            char flag = ' ';
            if(!getSession().getTransaction().isActive())
            {
                begin();
            flag = 'X';
            }
            Transaction transaction = (Transaction)getSession().get(Transaction.class, id);
           if(flag=='X'){
               getSession().flush();
            commit();
               getSession().clear();
           }
            if(transaction!=null){
                return transaction;
            }else{
                return null;
            }
        }catch(HibernateException e){
            rollback();
            throw new Exception("Transaction not found: "+e.getMessage());
        }

    }

    public int authorizeUser(UUID txId, User user) throws Exception{
        try{
            Transaction tx = getTransactionById(txId);
            if(tx != null){
                if(tx.getUser().getId() == user.getId()){
                    getSession().flush();
                    getSession().clear();
                    return 2;
                }
                else{
                    return 3;
                }

            }
            else{
                return 3;
            }
        }
        catch(HibernateException hex){
            rollback();
            throw new Exception("Unauthorized Access");
        }
    }

    public int deleteTransaction(UUID id) throws Exception{
        try{
            begin();
            Transaction tx = getTransactionById(id);
            if(tx!=null){
                getSession().flush();
            getSession().delete(tx);
            commit();
                getSession().clear();
            return 2;
            }
            return 1;
        } catch(HibernateException e){
            rollback();
            throw new Exception("Transaction not saved: "+e.getMessage());
        }

    }

    public int editTransaction(Transaction tx)throws Exception{
        try{
            begin();
            getSession().flush();
            getSession().saveOrUpdate(tx);
            commit();
            getSession().clear();
            return 2;
        }catch (HibernateException e){
            rollback();
            throw new Exception("Exception while updating transaction"+e.getMessage());
        }

    }

    public List<Transaction> getAllTransaction(UUID id)throws Exception{
        try{
            begin();
            Query q = getSession().createQuery("from Transaction where user_id = :id");
            q.setParameter("id", id);
            List<Transaction> list = q.getResultList();
            getSession().flush();
            commit();
            getSession().clear();
            return list;

        }catch (HibernateException e){
            rollback();
            throw new Exception("Exception while getting transactions"+e.getMessage());
        }
    }
}
