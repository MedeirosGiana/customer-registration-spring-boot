package gm.customer.registration.service;

import gm.customer.registration.dto.ClientDTO;
import gm.customer.registration.entity.Client;
import gm.customer.registration.repository.ClientRepository;
import gm.customer.registration.service.exceptions.DatabaseException;
import gm.customer.registration.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest){
        Page<Client> list = repository.findAll(pageRequest);
        return list.map(x -> new ClientDTO(x));
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> obj = repository.findById(id);
        Client entity = obj.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO insert(ClientDTO clientDTO) {
        Client entity = new Client();
        copyDtoToEntity(clientDTO, entity);
        entity = repository.save(entity);
        return new ClientDTO(entity);
    }
    @Transactional
    public ClientDTO update(Long id, ClientDTO clientDTO) {
        try {
            Client entity = repository.getReferenceById(id);
            copyDtoToEntity(clientDTO,entity);
            return new ClientDTO(entity);
        }catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Id não encontrado "+ id);
        }
    }

    public void  delete(Long id){
        try {
            repository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id não exixtente "+ id);
        }catch (DataIntegrityViolationException e){
            throw  new DatabaseException("Violação de integridade");
        }
    }


    private void copyDtoToEntity(ClientDTO clientDTO, Client entity) {
        entity.setName(clientDTO.getName());
        entity.setCpf(clientDTO.getCpf());
        entity.setIncome(clientDTO.getIncome());
        entity.setBirthDate(clientDTO.getBirthDate());
        entity.setChildren(clientDTO.getChildren());
    }


}
