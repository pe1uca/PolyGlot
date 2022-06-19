# Noun classes  

*From [wikipedia](https://en.wikipedia.org/wiki/Noun_class)*  

> a noun class is a particular category of nouns. A noun may belong to a given class because of the characteristic features of its referent, such as gender, animacy, shape, but such designations are often clearly conventional. [...]  
> Noun classes form a system of grammatical agreement.  

Example from the same wikipedia page  

> Modern English expresses noun classes through the third person singular personal pronouns *he* (male person), *she* (female person), and *it* (object, abstraction, or animal), and their other inflected forms. Countable and uncountable nouns are distinguished by the choice of *many*/*much*. The choice between the relative pronoun *who* (persons) and *which* (non-persons) may also be considered a form of agreement with a semantic noun class. A few nouns also exhibit vestigial noun classes, such as *stewardess*, where the suffix -*ess* added to *steward* denotes a female person. This type of noun affixation is not very frequent in English, but quite common in languages which have the true grammatical gender.  

<p align="center">  
	<img src="../../img/noun_classes.jpg" alt="Noun classes" width="250"/>  
</p>  

## Adding a noun class  

In the bottom right corner you'll find the "+" button to add a class to your conlang.  
When you click it you'll be taken to the [class info screen](#class-info-screen), where you'll be able to add the information about the class.  

## Editing an existing noun class  

Once you have a class listed in for your conlang, you'll be able to click on it to open the [class info screen](#class-info-screen) to manage its properties.  

## Class info screen  

<p align="center">  
	<img src="../../img/class_info.png" alt="noun class info" width="250"/>  
</p>  

### Class properties  

- **Class name**  
	The name to identify this class.  

- **Free text value**  
	Makes this class to have a text box instead of a select from a list of values.  
	This disables the "Values list" section below and the "Add value" button.  

- **Associative**  
	Makes this class to be a link with another word in your lexicon.  
	This disables the "Values list" section below and the "Add value" button.  

- **Applies to**  
	List of PoS that will have this class available in [lexeme info screen](lexicon.md#lexeme-info-screen).  

- **Values list**  
	List of values available for this class.  
	If the class is "Free text" or "Associative" this is unavailable.

<div class="admonition note">  
	<p class="admonition-title">Note</p>  
	<p>  
		A class can only have a list of values, be associative or be free text.<br>  
		These options are mutually exclusive, only one behavior can be configured at a time.  
	</p>  
</div>  

#### Applies to  

This section lists all the [Parts of speech](pos.md) that the language has configured.  
The class can be configured to only a few PoS or all of them.  

<p align="center">  
	<img src="../../img/class_applies.png" alt="Applies to few" width="250"/>  
	<img src="../../img/class_applies_all.png" alt="Applies to all" width="250"/>  
</p>  

## Classes in lexeme info screen  

### Normal class  

The default behavior of a class is to list the values that had been configured.  

<p align="center">  
	<img src="../../img/class_default.png" alt="Default class" width="250"/>  
</p>  

### Free text class  

This type of class gives a field to type any value.  

<p align="center">  
	<img src="../../img/class_free.png" alt="Free text class" width="250"/>  
</p>  

### Associative class  

This type of class opens your lexicon to select a lexeme that will show as the value.  

<p align="center">  
	<img src="../../img/class_assoc.png" alt="Associative class" width="250"/>  
	<img src="../../img/class_assoc_select.png" alt="Select associative lexeme" width="250"/>  
</p>  