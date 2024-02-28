package project.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.datajpa.dto.MemberDto;
import project.datajpa.entity.Member;
import project.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;
    
    @Test
    public void testMember(){

        Member member = new Member("TEST");

        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD(){

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();

        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        Long count = memberRepository.count();

        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long deletedCount = memberRepository.count();

        assertThat(deletedCount).isEqualTo(0);


    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findAllBy(){

        memberRepository.findAllBy();

    }

    @Test
    public void findUser(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(member1);

    }

    @Test
    public void findUsernameList(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        for(String username : usernameList){
            System.out.println("username : " + username);
        }

    }

    @Test
    public void findMemberDto(){

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10);
        member.changeTeam(team);
        memberRepository.save(member);

        List<MemberDto> MemberDto = memberRepository.findMemberDto();

        for (MemberDto dto : MemberDto){
            System.out.println("Result : " + dto);
        }

    }

    @Test
    public void findByNames(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for(Member member : result){
            System.out.println("Member : " + member);
        }

    }

    @Test
    public void returnType(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //result가 Null일 때 Empty Collection으로 반환 해준다.
        List<Member> resultList = memberRepository.findListByUsername("AAA");

        //단건일 경우는 Null로 반환
        Member result = memberRepository.findMemberByUsername("AAA");

        Optional<Member> resultOptional = memberRepository.findOptionalByUsername("AAA");


    }

    @Test
    public void paging(){

        //Given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        //When
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //Then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        /*
         * 페이징 없이 더 보기 기능을 사용할 때
        //When
        Slice<Member> slice = memberRepository.findByAgeSlice(age, pageRequest);

        //Then
        List<Member> sliceContent = page.getContent();

        assertThat(sliceContent.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue(); */

    }

    @Test
    public void bulkUpdate(){

        //Given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        //When
        int resultCount = memberRepository.bulkAgePlus(30);

        Member result = memberRepository.findMemberByUsername("member5");

        //Then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy(){

        //Given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //When
        //SELECT Member m
        //List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        //List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for(Member member : members){
            System.out.println("Member : " + member.getUsername());
            System.out.println("Member Team: " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint(){

        //Given
        Member member1 = new Member("member1", 10);

        memberRepository.save(member1);

        em.flush();
        em.clear();

        //When
        //Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());

        findMember.setUserName("member2");

        em.flush();

    }

    @Test
    public void queryLock(){

        //Given
        Member member1 = new Member("member1", 10);

        memberRepository.save(member1);

        em.flush();
        em.clear();

        //When
        Member findMember = memberRepository.findLockByUsername(member1.getUsername());

    }

    @Test
    public void callCustom(){

        List<Member> result = memberRepository.findMemberCustom();

    }

    @Test
    public void nativeQuery(){

        //Given
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //When
        Member result = memberRepository.findByNativeQuery("member1");

        System.out.println(result);

    }

    @Test
    public void nativeProjectionQuery(){

        //Given
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //When
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();

        for(MemberProjection memberProjection : content){
            System.out.println("memberProjection : " + memberProjection.getUsername());
            System.out.println("memberProjection : " + memberProjection.getTeamName());
        }

    }

}