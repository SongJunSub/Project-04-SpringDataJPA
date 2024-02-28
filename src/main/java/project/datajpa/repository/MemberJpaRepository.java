package project.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import project.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){

        em.persist(member);

        return member;

    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){

        return em.createQuery("SELECT m FROM Member m", Member.class).getResultList();

    }

    public Optional<Member> findById(Long id){

        Member member = em.find(Member.class, id);

        return Optional.ofNullable(member);

    }

    public Long count(){

        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class).getSingleResult();

    }

    public Member find(Long id){

        return em.find(Member.class, id);

    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age){

        return em.createQuery(
                "SELECT m" +
                        " FROM Member m" +
                        " WHERE m.username = :username" +
                        " AND m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();

    }

    public List<Member> findByPage(int age, int offset, int limit){

        return em.createQuery("SELECT m FROM Member m WHERE m.age = :age ORDER BY m.username DESC")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }

    public Long totalCount(int age){

        return em.createQuery("SELECT COUNT(m) FROM Member m WHERE m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();

    }

    public int bulkAgePlus(int age){

        return em.createQuery("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();

    }

}
