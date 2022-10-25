package com.ashish.library.Interfaces;

import com.ashish.library.Models.Member;

public interface MemberRepository {
    boolean addMember(Member member);
    boolean removeMember(String memberId);
    boolean modifyMember(Member member);
    Member getMember(String memberId);
}
