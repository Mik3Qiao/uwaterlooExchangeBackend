package infrastructure;

import domain.profile.CustomerProfile;
import domain.profile.CustomerProfileService;
import infrastructure.dto.ApiResponse;
import infrastructure.dto.in.profile.CreateCustomerProfileDto;
import infrastructure.dto.in.profile.UpdateCustomerProfileDto;
import infrastructure.dto.out.profile.CustomerProfilesDetailsDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
@Path("v1/api/profile")
@Produces("application/json")
public class CustomerProfileResources {
    @Inject
    CustomerProfileService customerProfileService;

    @POST
    @Path("/create-profile")
    public Response createCustomerProfile(CreateCustomerProfileDto createCustomerProfileDto) {
        CustomerProfilesDetailsDto createdCustomerProfile =
                CustomerProfilesDetailsDto.fromDomain(customerProfileService.createProfile(createCustomerProfileDto.toDomain()));
        ApiResponse<CustomerProfilesDetailsDto> response = new ApiResponse<>(Optional.empty(),
                                                                             200,
                                                                             Optional.of(createdCustomerProfile));
        return Response.ok(response).build();
    }

    @POST
    @Path("/get-profile/{customerId}")
    public Response getCustomerProfile(@PathParam("customerId") String customerId) {
        Optional<CustomerProfile> fetchedCustomerProfile =
                customerProfileService.getCustomerProfile(customerId);

        ApiResponse<CustomerProfilesDetailsDto> response = new ApiResponse<>(Optional.empty(),
                                                                             200,
                                                                             Optional.empty());

        if (fetchedCustomerProfile.isPresent()) {
            response.setData(Optional.of(CustomerProfilesDetailsDto.fromDomain(
                    fetchedCustomerProfile.get())));
        } else {
            //response.setMessage(Optional.of("Cannot find customer with id " + customerId + "."));
            throw new BadRequestException("Cannot find customer with ID" + customerId + ".");
        }

        return Response.ok(response).build();
    }

    @POST
    @Path("/update-profile")
    public Response updateCustomerProfile(UpdateCustomerProfileDto updateCustomerProfileDto) {
        Optional<CustomerProfile> updatedCustomerProfile =
                customerProfileService.updateCustomerProfile(updateCustomerProfileDto.toDomain());
        ApiResponse<CustomerProfilesDetailsDto> response = new ApiResponse<>(Optional.empty(),
                                                                             200,
                                                                             Optional.empty());
        if (updatedCustomerProfile.isEmpty()) {
            throw new BadRequestException("Cannot find listing with ID" + updateCustomerProfileDto.id() + ".");
            //response.setMessage(Optional.of("Cannot find customer with id " + updateCustomerProfileDto.id() + "."));
        } else {
            response.setData(Optional.of(CustomerProfilesDetailsDto.fromDomain(updatedCustomerProfile.get())));

        }
        return Response.ok(response).build();
    }

    @POST
    @Path("/delete-profile/{customerId}")
    public Response deleteCustomerProfile(@PathParam("customerId") String customerId) {
        ApiResponse<CustomerProfilesDetailsDto> response = new ApiResponse<>(Optional.empty(),
                                                                             200,
                                                                             Optional.empty());

        Optional<CustomerProfile> deletedCustomerProfile = customerProfileService.deleteCustomerProfile(customerId);
        if (deletedCustomerProfile.isEmpty()) {
            //response.setMessage(Optional.of("Cannot find customer with id " + customerId + ", as it's not found."));
            throw new BadRequestException("Cannot delete customer profile with ID" + customerId + ", as it's not found.");
        } else {
            response.setData(Optional.of(CustomerProfilesDetailsDto.fromDomain(deletedCustomerProfile.get())));
        }

        return Response.ok(response).build();
    }

}
