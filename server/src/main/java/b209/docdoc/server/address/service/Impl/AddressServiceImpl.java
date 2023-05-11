package b209.docdoc.server.address.service.Impl;

import b209.docdoc.server.address.dto.AddressInfo;
import b209.docdoc.server.address.dto.Request.AddressEditorReq;
import b209.docdoc.server.address.dto.Request.AddressRegisterReq;
import b209.docdoc.server.address.dto.Response.AddressListRes;
import b209.docdoc.server.address.service.AddressService;
import b209.docdoc.server.config.security.auth.MemberDTO;
import b209.docdoc.server.entity.AddressBook;
import b209.docdoc.server.entity.Member;
import b209.docdoc.server.repository.AddressBookRepository;
import b209.docdoc.server.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private MemberRepository memberRepository;
    private AddressBookRepository addressBookRepository;

    private final String NOT_GROUP = "그룹없음";
    private final String NOT_PHONE = "번호없음";

    private HashSet<String> getMemberAddressEmailSet(String memberEmail) {
        Optional<Member> member = memberRepository.findByMemberEmail(memberEmail);
        if (member.isEmpty()) return null;

        List<AddressBook> list = addressBookRepository.findAllByMember(member.get());
        HashSet<String> results = new HashSet<String>();
        for (AddressBook address: list) {
            results.add(address.getAddresEmail());
        }

        return results;
    }
    @Override
    public String saveOneAddress(AddressRegisterReq req, MemberDTO member) {
//        Optional<Member> member = memberRepository.findByMemberEmail(memberEmail);
        if (member.getIsDeleted()) return null;

        Optional<Member> memberObj = memberRepository.findByMemberEmail(member.getEmail());
        if (memberObj.isEmpty()) return null;

        addressBookRepository.save(
                AddressBook.builder()
                        .member(memberObj.get())
                        .addressName(req.getName())
                        .addresEmail(req.getEmail())
                        .addressPhone(req.getPhone() != null ? req.getPhone() : NOT_PHONE)
                        .addressGroup(req.getGroup() != null ? req.getGroup() : NOT_GROUP)
                        .addressIsDeleted(false)
                        .build()
        );

        return null;
    }

    @Override
    public AddressListRes getAddressListByGroup(String group, MemberDTO member) {
        List<AddressBook> list = new ArrayList<>();
        List<AddressInfo> result = new ArrayList<>();

        Optional<Member> memberObj = memberRepository.findByMemberEmail(member.getEmail());
        if (memberObj.isEmpty()) return null;

        if (group.equals(NOT_GROUP)) list = addressBookRepository.findAllByMember(memberObj.get());
        else list = addressBookRepository.findAllByMemberAndAddressGroup(memberObj.get(), group);

        for (AddressBook address: list) {
            result.add(new AddressInfo(
                    address.getAddressName(),
                    address.getAddresEmail(),
                    address.getAddressPhone(),
                    address.getAddressGroup()));
        }

        return AddressListRes.of(result);
    }

    @Override
    public AddressListRes getAddressBoolListByName(String name, MemberDTO member) {
        List<AddressBook> list = addressBookRepository.findAllByAddressNameStartingWith(name);
        List<AddressInfo> result = new ArrayList<>();

        for (AddressBook address: list) {
            result.add(new AddressInfo(
                    address.getAddressName(),
                    address.getAddresEmail(),
                    address.getAddressPhone(),
                    address.getAddressGroup()
            ));
        }

        return AddressListRes.of(result);
    }

    @Override
    public String saveAddressEditor(AddressEditorReq req, MemberDTO member) {

        Optional<Member> memberObj = memberRepository.findByMemberEmail(member.getEmail());
        if (memberObj.isEmpty()) return null;

        HashSet<String> emails = getMemberAddressEmailSet(member.getEmail());

        for (AddressInfo address: req.getAddresses()) {
            if (emails != null && address.getEmail() != null && !emails.contains(address.getEmail())) {
                addressBookRepository.save(
                        AddressBook.builder()
                                .member(memberObj.get())
                                .addressName(address.getName())
                                .addresEmail(address.getEmail())
                                .addressPhone(address.getPhone() != null ? address.getPhone() : NOT_PHONE)
                                .addressGroup(address.getGroup() != null ? address.getGroup() : NOT_GROUP)
                                .addressIsDeleted(false)
                                .build()
                );
            }
        }

        return null;
    }
}
