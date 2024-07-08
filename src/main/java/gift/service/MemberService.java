package gift.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import gift.dto.MemberDto;
import gift.dto.request.LoginRequest;
import gift.entity.Member;
import gift.exception.CustomException;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;

@Service
public class MemberService {

    private MemberRepository memberRepository;
    private JwtUtil jwtUtil;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil){
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public MemberDto findById(Long id){
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new CustomException("Member with id " + id + " not found", HttpStatus.NOT_FOUND));
        return member.toDto();
    }

    public void addUser(MemberDto memberDto){

        if(memberRepository.findById(memberDto.getId()).isEmpty()){
            Member member = memberDto.toEntity(memberDto);
            memberRepository.save(member);
        }else{
            throw new CustomException("Member with id " + memberDto.getId() + "exists", HttpStatus.CONFLICT);
        }
    }

    public void deleteMember(Long id){
        memberRepository.deleteById(id);
    }

    public MemberDto findByEmail(String email){
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException("Member with email " + email + " not found", HttpStatus.NOT_FOUND));
        return member.toDto();
    }

    public MemberDto findByRequest(LoginRequest loginRequest){
        Member member = memberRepository.findByRequest(loginRequest.getEmail(), loginRequest.getPassword())
            .orElseThrow(() -> new CustomException("User with Request not found", HttpStatus.NOT_FOUND));
        return member.toDto();
    }

    public String generateToken(String email){
        MemberDto memberDto = findByEmail(email);
        return jwtUtil.generateToken(memberDto);
    }

    public String authenticateUser(LoginRequest loginRequest){
        MemberDto memberDto = findByRequest(loginRequest);
        return generateToken(memberDto.getEmail());
    }
    
}