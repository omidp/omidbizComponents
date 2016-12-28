module Merb
  module GlobalHelpers
    # Returns true if the app is running as a slice, false otherwise
    def is_slice?
      ::Feather.respond_to?(:public_path_for)
    end
    
    # This wraps whether the app is a slice or not, and then will create the appropriate stylesheet include
    def css_include(*args)
      output = ""
      args.each do |stylesheet|
        output << (is_slice? ? "<link type=\"text/css\" href=\"#{File.join(::Feather.public_path_for(:stylesheet), stylesheet.to_s + ".css")}\" charset=\"utf-8\" rel=\"Stylesheet\" media=\"all\" />" : css_include_tag(stylesheet.to_s))
      end
      output
    end
    
    # This wraps whether the app is a slice or not, and then will create the appropriate js include
    def js_include(*args)
      output = ""
      args.each do |javascript|
        output << (is_slice? ? "<script type=\"text/javascript\" src=\"#{File.join(::Feather.public_path_for(:javascript), javascript.to_s + ".js")}\"></script>" : js_include_tag(javascript.to_s))
      end
      output
    end
    
    def render_title
      @settings[:title]
    end

    def render_tag_line
      @settings[:tag_line]
    end

    ##
    # This formats the specified text using the specified formatter, returning the result
    def render_text(formatter, text)
      ::Feather::Hooks::Formatters.format_text(formatter, text)
    end

    ##
    # This formats the specified article, using it's configured formatter, returning the result
    def render_article(article)
      ::Feather::Hooks::Formatters.format_article(article)
    end

    ##
    # This builds the menu list from the menu items, returning the markup
    def render_menu
      menu = ""
      menu_items.each_with_index do |item, index|
      	menu += "<li>#{link_to item[:text], item[:url]} #{render_link_dot(index, menu_items.size - 1)}</li>"
      end
      menu
    end

    ##
    # This returns the date as a relative date (today, yesterday etc)
    def render_relative_date(date)
      date = Date.parse(date, true) unless /Date.*/ =~ date.class.to_s
      days = (date - Date.today).to_i

      return 'today' if days >= 0 and days < 1
      return 'tomorrow' if days >= 1 and days < 2
      return 'yesterday' if days >= -1 and days < 0

      return "in #{days} days" if days.abs < 60 and days > 0
      return "#{days.abs} days ago" if days.abs < 60 and days < 0

      return date.strftime('%A, %B %e') if days.abs < 182
      return date.strftime('%A, %B %e, %Y')
    end

    ##
    # This returns the contents of the notifications in the session, clearing it at the same time
    def notifications
      notifications = session[:notifications]
      session[:notifications] = nil
      notifications
    end

    ##
    # This returns the names of all possible timezones
    def get_timezones
      TZInfo::Timezone.all.collect { |tz| tz.name }
    end

    ##
    # This returns the published at date for an article as a relative date (taking into account timezones)
    def render_relative_published_at(article)
      article.published_at.nil? ? "Not yet" : render_relative_date(TZInfo::Timezone.get(logged_in? ? session.user.time_zone : article.user.time_zone).utc_to_local(article.published_at))
    end

    ##
    # This returns the markup for the about text in the sidebar
    def render_about_text
      unless @settings.nil? || @settings[:about].blank?
        markup = <<-MARKUP
        <div class="sidebar-node">
          <h3>About</h3>
          <p>#{render_text(@settings[:about_formatter], @settings[:about])}</p>
        </div>
        MARKUP
      end
      markup
    end

    ##
    # This returns the url for the year article index
    def year_url(year)
      url(:year, {:year => year})
    end

    ##
    # This returns the url for the month article index
    def month_url(year, month)
      url(:month, {:year => year, :month => ::Feather::Padding::pad_single_digit(month)})
    end

    ##
    # This returns the url for the day article index
    def day_url(year, month, day)
      url(:day, {:year => year, :month => ::Feather::Padding::pad_single_digit(month), :day => ::Feather::Padding::pad_single_digit(day)})
    end

    ##
    # This returns all menu items, including those provided by plugins
    def menu_items
      items = []
      items << {:text => "Dashboard", :url => url(:admin_dashboard)}
      items << {:text => "Articles", :url => url(:admin_articles)}
      items << {:text => "Plugins", :url => url(:admin_plugins)}
      items << {:text => "Users", :url => url(:admin_users)}
      items << {:text => "Settings", :url => url(:admin_configuration)}
      root_url = url(:admin_dashboard).gsub("/admin/dashboard", "")
      ::Feather::Hooks::Menu.menu_items.each { |item| items << item.merge(:url => "#{root_url}#{item[:url]}") }
      items << {:text => "Logout", :url => url(:logout)}
      items
    end

    ##
    # This either adds the breaking • character unless we're at the end of the collection (based on index and size)
    def render_link_dot(index, collection_size)
      "&nbsp;•" unless index == collection_size
    end

    ##
    # This renders all plugin views for the specified hook
    def render_plugin_views(name, options = {})
      output = ""
      ::Feather::Hooks::View.plugin_views.each do |view|
        if view[:name] == name
          if view[:partial]
            # Set the template root, create the template method and call the partial
            _template_root = File.join(view[:plugin].path.gsub(Merb.root, "."), "views")
            template_location = _template_root / _template_location("#{view[:partial]}", content_type, view[:name])
            # Make the location relative to the root
            template_location = "../../" / template_location
            output << partial(template_location, { :with => options[:with], :as => options[:with].class.to_s.downcase.singular.split("::").last })
          else
            # Render the specified text using ERB and the options
            output << Proc.new { |args| ERB.new(view[:content]).result(binding) }.call(options[:with])
          end
        end
      end
      # Return the view markup generated by plugins
      output
    end

    ##
    # This returns the full url for an article
    def get_full_url(article)
      "http://#{request.host}#{article.permalink}"
    end

    ##
    # This escapes the specified url
    def escape_url(url)
      CGI.escape(url)
    end

    ##
    # This returns true if the specified plugin is active, false if it isn't or is unavailable
    def is_plugin_active(name)
      plugin = ::Feather::Plugin.get(name)
      plugin && plugin.active
    end

    def link_to_article(text, article)
      if is_slice? && !::Feather.config[:path_prefix].empty?
        link_to(text, '/' + ::Feather.config[:path_prefix] + article.permalink)
      else
        link_to(text, article.permalink)
      end
    end

    def link_to_author(author)
      link_to author["name"], author["homepage"]
    end
    
    # This returns true if the user is logged in, false otherwise
    def logged_in?
      !session.user.nil?
    end
  end
end
