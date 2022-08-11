package eu.zinovi.receipts.service.impl;

import eu.zinovi.receipts.domain.model.entity.Address;
import eu.zinovi.receipts.repository.AddressRepository;
import eu.zinovi.receipts.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressServiceImpl implements AddressService {


    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    @Override
    public Address getAddress(String value) {
        Address address = addressRepository.findByValue(value).orElse(null);

        if (address == null) {
            address = new Address();
            address.setValue(value);

            Matcher matcher = Pattern.compile("бул|ул").matcher(value.toLowerCase());
            if (matcher.find()) {
                address.setCity(value.substring(0, matcher.start())
                        .replace(" ", "")
                        .replace(",", ""));
                address.setStreet(value.substring(matcher.start()));
            }
            addressRepository.save(address);
        }
        return address;
    }


}
