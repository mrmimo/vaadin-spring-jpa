# vaadin-spring-jpa

This project glues spring, jpa, vaadin together to create quick Domain Driven Development environment. 

## Story behind

There are existing projects for creating  interface to jpa-modelled applications. Examples:

### OpenXava: https://www.openxava.org/ 

It does what it says - it's a complete environment for building applications based on JPA. 
What I really don't like in openxava - you need to write your JPA code specifically for  Openxava. 
I.e. you have to introduce strong dependency to Openxava directly in your JPA code. 
You cannot connect JPA library to openxava and add views to the mix. You implement views inside JPA
definitions.

I tried running my existing JPA model under Xava. It worked - it took me a day to annotate my domain classes
with view definition annotations. 

In the longer run I would have to double the effort related to maintaining my JPA so it could work inside and outside of xava. 

### Apache Causeway (formerly ISIS)

That's better - it does Domain Driven Development as it should be done. It provides GUI for your model based on Wicket. 
What I don't like is - it's not that easy to start with causeway. I sacrified two days for trying Apache Causeway.
I ran some examples - it worked. But then I tried to move some existing JPA domain to Causeway and I was disappointed. 

At first I was not able to disable authentication (Bypass security provider didn't work). Then I discovered how painful
the development was. Every time I changed one thing, I had go through authorization, and usually it didn't worked 
until I deleted cookies from previous run. That process was very slow.

I haven't found anything better.

## Spring, JPA... Vaadin

My previous attempts on creating new projects for administrative backends was based mostly on Spring, Spring-data, JPA repositories accessible via REST and GUI built on react.
I used react-admin recently and it is a nice framework.

The problem with this kind of development that your model and your views are loosly coupled. That means that if you introduce a new field
to your domain object you have to implement it at least twice - i.e. you add it to your domain, and then you add it to your views. Very often there is more 
than one view. So you end up with a lot of redundant code. 

Let's say you want to model an ecommerce system. Field named price, containing BigDecimal is present in many objects like Product, OrderLine, InvoiceLine, etc. 
Each time, when you define a form you have to verbosely mention informations that are already present in your model.

I thought that I would rather like to define my form like this:

```java
class ProductForm extends ....  {
   public ProductForm() {
      super( Product.class, // <- domain object
             new String[] { "name", "description", "price", "tax" } // <- fields I would like in my form
             );
}               
```

So I decided to build something like this that uses Vaadin for GUI, JPA, spring-data for JPA repositories and spring for dependency injection.

In the above example ProductForm is automatically injected with ProductRepository, but it also is automatically configured with apropriate GUI 
fields that are matched to the data present in domain objects.
