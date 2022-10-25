package com.ashish.library.DataBase;

import com.ashish.library.Interfaces.MemberRepository;
import com.ashish.library.Models.Member;

import java.util.HashMap;
import java.util.Map;

public class MemberDataBase implements MemberRepository {
    private Map<String, Member> memberMap = new HashMap<>();
    private static MemberDataBase memberDataBase;

    private MemberDataBase (){}

    public static MemberDataBase getMemberDataBase() {
        if(memberDataBase == null) {
            memberDataBase = new MemberDataBase();
        }
        return  memberDataBase;
    }

    @Override
    public boolean addMember(Member member) {
        if(!memberMap.containsKey(member.getMemberId())){
            System.out.println("member Already exist with this memberID =" + member.getMemberId() );
            return  false;
        } else {
            memberMap.put(member.getMemberId(), member);
            return true;
        }
    }

    @Override
    public boolean removeMember(String memberId) {
        memberMap.remove(memberId);
        return true;
    }

    @Override
    public boolean modifyMember(Member member) {
            if(!memberMap.containsKey(member.getMemberId())){
                System.out.println("No member exist with this memberID =" + member.getMemberId() );
                return  false;
            } else {
                memberMap.put(member.getMemberId(), member);
                return true;
            }
    }

    @Override
    public Member getMember(String memberId) {
        return memberMap.get(memberId);
    }
}
