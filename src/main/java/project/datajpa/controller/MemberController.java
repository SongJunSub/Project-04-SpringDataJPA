package project.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import project.datajpa.dto.MemberDto;
import project.datajpa.entity.Member;
import project.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){

        Member member = memberRepository.findById(id).get();

        return member.getUsername();

    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){

        return member.getUsername();

    }

    //http://localhost:8080/members?page=0&size=3&sort=username,desc
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){

        /*Page<Member> page = memberRepository.findAll(pageable);

        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        return map;*/

        //return memberRepository.findAll(pageable).map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        return memberRepository.findAll(pageable)
                .map(MemberDto::new);

    }

    //@PostConstruct
    public void init(){

        for(int i=0; i<100; i++){
            memberRepository.save(new Member("member" + i, i));
        }

    }

}