package com.ashish.library.Controllers;


import com.ashish.library.DataBase.MemberDataBase;
import com.ashish.library.Models.Member;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private MemberDataBase memberDataBase = MemberDataBase.getMemberDataBase();


    public boolean AddMember(Member member) {
     return  memberDataBase.addMember(member);

    }

    public boolean removeMember(String memberId) {
        return memberDataBase.removeMember(memberId);

    }

    public boolean modifyMember(Member member) {
        return memberDataBase.modifyMember(member);
    }

    public Member getMember(String memberId) {
        return memberDataBase.getMember(memberId);
    }
}
