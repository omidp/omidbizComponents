module Admin
  class Dashboard < Base
    skip_before :login_required
    before :check_for_user
    
    def index
      render
    end
    
    private
      ##
      # This checks to see if there are no users (such as when it's a fresh install) - if so,
      # it creates a default user and redirects the user to login with those details
      def check_for_user
        if User.count == 0
          User.create({:login => "admin", :password => "password", :password_confirmation => "password", :email => "none@none", :time_zone => "Europe/London"})
          notify "No users found, so default user created: authenticate with login \"admin\", password \"password\", and then change your password."
        end
        login_required
      end
  end
end