package project.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.datajpa.repository.MemberRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity(){

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 34, teamA);
        Member memberB = new Member("memberB", 32, teamA);
        Member memberC = new Member("memberC", 31, teamB);
        Member memberD = new Member("memberD", 30, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        //flush를 하게되면 강제로 DB에 Insert Call을 날린다.
        em.flush();
        //DB에 Insert Call 후 JPA의 em.context에 있는 캐시를 다 날려준다. (초기화)
        em.clear();

        //확인
        List<Member> members = em.createQuery("SELECT m FROM Member m", Member.class).getResultList();

        for(Member member : members){
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }


    }

    @Test
    public void JpaEventBaseEntity() throws Exception {

        //Given
        Member member = new Member("member1");

        memberRepository.save(member); //@PrePersist 발생

        Thread.sleep(300);

        member.setUserName("member2");

        em.flush(); //@PreUpdate 발생
        em.clear();

        //When
        Member findMember = memberRepository.findById(member.getId()).get();

        //Then
        System.out.println("Member : " + findMember.getCreatedDate());
        System.out.println("Member : " + findMember.getLastModifiedDate());
        System.out.println("Member : " + findMember.getCreatedBy());
        System.out.println("Member : " + findMember.getLastModifiedBy());

    }

}